export default {
  path: "/admin",
  name: "admin",
  meta: {
    authenticated: true,
  },
  component: () => import("@/admin/admin.view.vue"),
  children: [
    {
      path: "",
      name: "admin-users",
      meta: {
        authenticated: true,
      },
      component: () => import("@/admin/admin-users/admin-users.view.vue"),
    },
    {
      path: "roles",
      name: "admin-roles",
      meta: {
        authenticated: true,
      },
      component: () => import("@/admin/admin-roles/admin-roles.view.vue"),
    },
    {
      path: "users/:userMail",
      name: "admin-user",
      meta: {
        authenticated: true,
      },
      component: () => import("@/admin/admin-user/admin-user.view.vue"),
    },
  ],
};
