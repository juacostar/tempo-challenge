const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/tenpo/api";

export class ApiError extends Error {


    constructor(status, message) {
      super(message);
      this.name  = "ApiError";
      this.status = status;
    }
  }

  export async function http(path, init = {}) {
    const response = await fetch(`${BASE_URL}${path}`, {
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
        ...init.headers,
      },
      ...init,
    });
  
    if (!response.ok) {
      // Try to read a message from the response body, fall back to statusText
      let message = response.statusText;
      try {
        const body = await response.json();
        message = body?.message ?? message;
      } catch {
        // body wasn't JSON — keep statusText
      }
      throw new ApiError(response.status, message);
    }

    if (response.status === 204) return null;
  
    return response.json();
  }