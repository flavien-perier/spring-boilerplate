import { Buffer } from "buffer";
import * as scrypt from "scrypt-js";

class PasswordUtil {
  public encode(password: string, salt: string): string {
    const passwordBuffer = new Buffer(password);
    const saltBuffer = new Buffer(salt);

    const N = 4096;
    const r = 8;
    const p = 4;
    const dkLen = 32;

    return scrypt.syncScrypt(passwordBuffer, saltBuffer, N, r, p, dkLen).join("");
  }
}

const passwordUtil = new PasswordUtil();

export { passwordUtil };