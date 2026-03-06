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
  ]
};
