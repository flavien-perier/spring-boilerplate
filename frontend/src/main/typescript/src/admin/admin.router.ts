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
      path: "groups",
      name: "admin-groups",
      meta: {
        authenticated: true,
      },
      component: () => import("@/admin/admin-groups/admin-groups.view.vue"),
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
