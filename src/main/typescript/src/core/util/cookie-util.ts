class CookieUtil {
  public clearAll() {
    document.cookie = "";
  }

  public get(name: string): string | null {
    const cookies = document.cookie.split(';');
    for (const cookie of cookies) {
      const [cookieName, cookieValue] = cookie.trim().split('=');
      if (cookieName === name) {
        return cookieValue;
      }
    }
    return null;
  }
}

const cookieUtil = new CookieUtil();

export {cookieUtil};