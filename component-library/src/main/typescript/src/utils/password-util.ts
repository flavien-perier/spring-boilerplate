import { Buffer } from "buffer";
import * as scrypt from "scrypt-js";
class PasswordUtil {
  public async proofOfWork(password: string, salt: string): Promise<string> {
    const passwordBuffer = new Buffer(password);
    const saltBuffer = new Buffer(salt);

    const N = 4096;
    const r = 8;
    const p = 4;
    const dkLen = 32;

    return (await scrypt.scrypt(passwordBuffer, saltBuffer, N, r, p, dkLen))
      .join("");
  }

  public checkPassword(password: string, minLength: number = 8): boolean {
    return (
      this.checkPasswordLength(password, minLength) &&
      this.checkPasswordNumber(password) &&
      this.checkPasswordUppercase(password) &&
      this.checkPasswordLowercase(password) &&
      this.checkSpecialCharacter(password)
    );
  }

  public checkPasswordLength(password: string, minLength: number = 8): boolean {
    return password.length >= minLength;
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
