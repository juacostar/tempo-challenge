import { http } from "./httpclient";

export async function getHistory() {
    return http("/calls");
}
