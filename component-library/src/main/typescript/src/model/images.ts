import logoSvg from "../assets/img/logo.svg?raw";
import springSvg from "../assets/img/spring.svg?raw";
import kotlinSvg from "../assets/img/kotlin.svg?raw";
import javaSvg from "../assets/img/java.svg?raw";
import vueSvg from "../assets/img/vue.svg?raw";
import typescriptSvg from "../assets/img/typescript.svg?raw";
import openapiSvg from "../assets/img/openapi.svg?raw";
import dockerSvg from "../assets/img/docker.svg?raw";
import kubernetesSvg from "../assets/img/kubernetes.svg?raw";
import helmSvg from "../assets/img/helm.svg?raw";
import undrawHelloSvg from "../assets/img/undraw_hello.svg?raw";
import undrawLoginSvg from "../assets/img/undraw_login.svg?raw";
import undrawCreateAccountSvg from "../assets/img/undraw_create_account.svg?raw";
import undrawForgotPasswordSvg from "../assets/img/undraw_forgot_password.svg?raw";
import undrawAuthenticationSvg from "../assets/img/undraw_authentication.svg?raw";
import undrawSecurityOnSvg from "../assets/img/undraw_security_on.svg?raw";
import undrawUpdatesSvg from "../assets/img/undraw_updates.svg?raw";

export const IMAGES = {
  logo: logoSvg,
  spring: springSvg,
  kotlin: kotlinSvg,
  java: javaSvg,
  vue: vueSvg,
  typescript: typescriptSvg,
  openapi: openapiSvg,
  docker: dockerSvg,
  kubernetes: kubernetesSvg,
  helm: helmSvg,
  undraw_hello: undrawHelloSvg,
  undraw_login: undrawLoginSvg,
  undraw_create_account: undrawCreateAccountSvg,
  undraw_forgot_password: undrawForgotPasswordSvg,
  undraw_authentication: undrawAuthenticationSvg,
  undraw_security_on: undrawSecurityOnSvg,
  undraw_updates: undrawUpdatesSvg,
} as const;

export type ImageName = keyof typeof IMAGES;
