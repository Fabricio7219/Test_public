// Teste de carga da API Restful-Booker com k6.
// Fluxo por usuário virtual: cria reserva -> busca -> exclui.
//
// A carga é propositalmente baixa (até 5 usuários) porque a API é pública e gratuita:
// o objetivo é medir tempo de resposta e taxa de erro, não derrubar o serviço.
//
// Rodar local:  k6 run perf/booking-load.js
// Outra URL:    k6 run -e BASE_URL=http://localhost:3001 perf/booking-load.js

import http from 'k6/http';
import { check, group, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'https://restful-booker.herokuapp.com';

// Accept com um único tipo: com múltiplos tipos o POST /booking responde 418 (docs/BUGS.md, BUG-003)
const HEADERS = { 'Content-Type': 'application/json', Accept: 'application/json' };

export const options = {
  scenarios: {
    carga: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '10s', target: 5 }, // sobe para 5 usuários
        { duration: '30s', target: 5 }, // mantém
        { duration: '10s', target: 0 }, // desce
      ],
      gracefulRampDown: '5s',
    },
  },
  // Critérios de aceite: se algum falhar, o k6 sai com erro e o pipeline fica vermelho
  thresholds: {
    http_req_failed: ['rate<0.01'],
    checks: ['rate>0.99'],
    http_req_duration: ['p(95)<2000'],
    'http_req_duration{endpoint:criar}': ['p(95)<2500'],
    'http_req_duration{endpoint:buscar}': ['p(95)<1500'],
    'http_req_duration{endpoint:excluir}': ['p(95)<1500'],
  },
};

/** Executado uma vez antes da carga: gera o token usado para excluir as reservas. */
export function setup() {
  const res = http.post(
    `${BASE_URL}/auth`,
    JSON.stringify({ username: 'admin', password: 'password123' }),
    { headers: HEADERS },
  );
  const ok = check(res, { 'auth 200': (r) => r.status === 200 && !!r.json('token') });
  if (!ok) {
    throw new Error(`Falha ao gerar token: ${res.status} ${res.body}`);
  }
  return { token: res.json('token') };
}

function novaReserva() {
  const checkin = new Date(Date.now() + Math.floor(Math.random() * 60 + 1) * 86400000);
  const checkout = new Date(checkin.getTime() + 2 * 86400000);
  return {
    firstname: 'Perf',
    lastname: `K6-${__VU}-${__ITER}`,
    totalprice: Math.floor(Math.random() * 1900) + 100,
    depositpaid: true,
    bookingdates: {
      checkin: checkin.toISOString().slice(0, 10),
      checkout: checkout.toISOString().slice(0, 10),
    },
    additionalneeds: 'Cafe da manha',
  };
}

export default function (data) {
  let id;

  group('criar reserva', () => {
    const res = http.post(`${BASE_URL}/booking`, JSON.stringify(novaReserva()), {
      headers: HEADERS,
      tags: { endpoint: 'criar' },
    });
    check(res, {
      'criar: status 200': (r) => r.status === 200,
      'criar: retorna id': (r) => r.status === 200 && r.json('bookingid') > 0,
    });
    if (res.status === 200) {
      id = res.json('bookingid');
    }
  });

  if (!id) {
    sleep(1);
    return;
  }

  group('buscar reserva', () => {
    const res = http.get(`${BASE_URL}/booking/${id}`, {
      headers: HEADERS,
      tags: { endpoint: 'buscar' },
    });
    check(res, { 'buscar: status 200': (r) => r.status === 200 });
  });

  group('excluir reserva', () => {
    const res = http.del(`${BASE_URL}/booking/${id}`, null, {
      headers: { ...HEADERS, Cookie: `token=${data.token}` },
      tags: { endpoint: 'excluir' },
    });
    // A API responde 201 no DELETE (docs/BUGS.md, BUG-002)
    check(res, { 'excluir: status 201': (r) => r.status === 201 });
  });

  sleep(1);
}

/** Gera um resumo em Markdown para o pipeline, além da saída padrão no terminal. */
export function handleSummary(data) {
  const m = data.metrics;
  const ms = (v) => (v === undefined ? '-' : `${Math.round(v)} ms`);
  const linha = (nome, metrica) => {
    const v = (metrica && metrica.values) || {};
    return `| ${nome} | ${ms(v.avg)} | ${ms(v.med)} | ${ms(v['p(95)'])} | ${ms(v.max)} |`;
  };

  const limites = Object.entries(m)
    .filter(([, metrica]) => metrica.thresholds)
    .flatMap(([nome, metrica]) =>
      Object.entries(metrica.thresholds).map(
        ([regra, r]) => `| \`${nome}\` | \`${regra}\` | ${r.ok ? 'Aprovado' : 'Reprovado'} |`,
      ),
    );

  const md = [
    '## Teste de performance (k6)',
    '',
    `Requisições: **${m.http_reqs.values.count}** · Iterações: **${m.iterations.values.count}** · ` +
      `Taxa de erro: **${(m.http_req_failed.values.rate * 100).toFixed(2)}%** · ` +
      `Checks aprovados: **${(m.checks.values.rate * 100).toFixed(2)}%**`,
    '',
    '### Tempo de resposta',
    '',
    '| Endpoint | Média | Mediana | p95 | Máximo |',
    '| --- | --- | --- | --- | --- |',
    linha('Todos', m.http_req_duration),
    linha('POST /booking', m['http_req_duration{endpoint:criar}']),
    linha('GET /booking/{id}', m['http_req_duration{endpoint:buscar}']),
    linha('DELETE /booking/{id}', m['http_req_duration{endpoint:excluir}']),
    '',
    '### Critérios de aceite',
    '',
    '| Métrica | Regra | Resultado |',
    '| --- | --- | --- |',
    ...limites,
    '',
  ].join('\n');

  return {
    'perf-summary.md': md,
    'perf-summary.json': JSON.stringify(data, null, 2),
    stdout: md + '\n',
  };
}
