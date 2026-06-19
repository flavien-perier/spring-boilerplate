class CookieUtil {
  public clearAll() {
    document.cookie.split(";").forEach((cookie) => {
      const name = cookie.trim().split("=")[0];
      if (name) {
        this.clear(name);
      }
    });
  }

  public clear(name: string) {
    document.cookie = `${name}=; max-age=0; path=/`;
  }

  public get(name: string): string | null {
    const cookies = document.cookie.split(";");
    for (const cookie of cookies) {
      const [cookieName, cookieValue] = cookie.trim().split("=");
      if (cookieName === name) {
        return cookieValue!;
      }
    }
    return null;
  }
}

const cookieUtil = new CookieUtil();

export { cookieUtil };
