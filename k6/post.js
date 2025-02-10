import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        constant_request_rate: {
            executor: 'constant-arrival-rate',
            rate: 50,
            timeUnit: '1s',
            duration: '5m',
            preAllocatedVUs: 20,
            maxVUs: 100,
        },
    },
};

export default function () {
    const url = `http://${__ENV.HOST}:8080/user`
    const payload = JSON.stringify({ first_name: "john", last_name: "smith" })
    const params = { headers: { 'Content-Type': 'application/json' } }

    let res = http.post(url, payload, params);
    check(res, { 'status was 201': (r) => r.status == 201 });
}
