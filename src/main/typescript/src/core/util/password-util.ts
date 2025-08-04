import { Buffer } from "buffer";
import * as scrypt from "scrypt-js";
import {useApplicationStore} from "@/core/application.store.ts";

class PasswordUtil {
  public proofOfWork(password: string, salt: string): string {
    const passwordBuffer = new Buffer(password);
    const saltBuffer = new Buffer(salt);

    const N = 4096;
    const r = 8;
    const p = 4;
    const dkLen = 32;

    return scrypt.syncScrypt(passwordBuffer, saltBuffer, N, r, p, dkLen).join("");
  }

  public checkPassword(password: string): boolean {
    return this.checkPasswordLength(password) &&
      this.checkPasswordNumber(password) &&
      this.checkPasswordUppercase(password) &&
      this.checkPasswordLowercase(password) &&
      this.checkSpecialCharacter(password);
  }

  public checkPasswordLength(password: string): boolean {
    return password.length >= useApplicationStore().configuration.minPasswordLength;
  }

  public checkPasswordNumber(password: string): boolean {
    return password.match(/[0-9]/) !== null;
  }

  public checkPasswordUppercase(password: string): boolean {
    return password.match(/[A-Z]/) !== null;
  }

  public checkPasswordLowercase(password: string): boolean {
    return password.match(/[a-z]/) !== null;
  }

  public checkSpecialCharacter(password: string): boolean {
    return password.match(/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/) !== null;
  }
}

const passwordUtil = new PasswordUtil();

export { passwordUtil };