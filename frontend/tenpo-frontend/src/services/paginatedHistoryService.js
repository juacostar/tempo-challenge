import { http } from "./httpclient";

export async function getPaginatedHistory(page, limit) {
    return http(`/calls/paginated?page=${page}&size=${limit}`);
}