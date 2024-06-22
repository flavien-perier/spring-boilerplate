import { createRouter, createWebHistory } from "vue-router";
import homeRouter from "@/home/home.router";
import loginRouter from "@/login/login.router";
import changePasswordRouter from "@/change-password/change-password.router";
import createAccountRouter from "@/create-account/create-account.router";
import forgotPasswordRouter from "@/forgot-password/forgot-password.router";
import accountRouter from "@/account/account.router";
import {useApplicationStore} from "@/core/application.store";
import adminRouter from "@/admin/admin.router.ts";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    homeRouter,
    loginRouter,
    changePasswordRouter,
    createAccountRouter,
    forgotPasswordRouter,
    accountRouter,
    adminRouter,
  ],
});

router.beforeEach((to, from, next) => {
  const applicationStore = useApplicationStore();

  if (to.meta.authenticated && !applicationStore.isAuthenticated) {
    router.push({ name: "login" });
  } else {
    next();
  }
});

export default router;
