import { http } from "./httpclient";

export async function postCalculation({ a, b}) {
    const payload = {
      a:        parseFloat(a),
      b:        parseFloat(b),
    };
  
    return http("/calculator/calculate/" + a + "/" + b, {
      method: "POST",
    });
  }