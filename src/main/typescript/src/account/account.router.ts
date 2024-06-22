export default {
  path: "/account",
  name: "account",
  meta: {
    authenticated: true,
  },
  component: () => import("@/account/account.view.vue"),
  children: [
    {
      path: "",
      name: "account-information",
      meta: {
        authenticated: true,
      },
      component: () => import("@/account/account-information/account-information.view.vue"),
    },
    {
      path: "security",
      name: "account-security",
      meta: {
        authenticated: true,
      },
      component: () => import("@/account/account-security/account-security.view.vue"),
    },
  ]
};
