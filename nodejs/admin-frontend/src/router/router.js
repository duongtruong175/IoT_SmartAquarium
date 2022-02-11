import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '../store/store'

// login page
import Login from '../layouts/Login.vue'

// admin page
import AppLayout from '../layouts/AppLayout.vue'
import Home from '../views/Home.vue'
import Dashboard from '../views/Dashboard.vue'
import UsersIndex from '../views/UsersIndex.vue'
import UsersCreate from '../views/UsersCreate.vue'
import UsersEdit from '../views/UsersEdit.vue'
import DevicesIndex from '../views/DevicesIndex.vue'
import DevicesCreate from '../views/DevicesCreate.vue'
import DevicesEdit from '../views/DevicesEdit.vue'

Vue.use(VueRouter)

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: Login,
        meta: {
            title: 'Login'
        }
    },
    {
        path: '/',
        component: AppLayout,
        children: [
            {
                path: '',
                name: 'Home',
                component: Home,
                meta: {
                    title: 'Home'
                }
            },
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: Dashboard,
                meta: {
                    title: 'Dashboard'
                }
            },
            {
                path: 'users',
                name: 'UsersIndex',
                component: UsersIndex,
                meta: {
                    title: 'User list'
                }
            },
            {
                path: 'users/create',
                name: 'UsersCreate',
                component: UsersCreate,
                meta: {
                    title: 'Add user'
                }
            },
            {
                path: 'users/:id/edit',
                name: 'UsersEdit',
                component: UsersEdit,
                meta: {
                    title: 'Edit user'
                }
            },
            {
                path: 'devices',
                name: 'DevicesIndex',
                component: DevicesIndex,
                meta: {
                    title: 'Device list'
                }
            },
            {
                path: 'devices/create',
                name: 'DevicesCreate',
                component: DevicesCreate,
                meta: {
                    title: 'Add device'
                }
            },
            {
                path: 'devices/:id/edit',
                name: 'DevicesEdit',
                component: DevicesEdit,
                meta: {
                    title: 'Edit device'
                }
            },
        ],
    }
];

const router = new VueRouter({
    mode: "history",
    routes
});

router.beforeEach((to, from, next) => {
    const user = store.getters.getUserAuth;
    if (to.name !== "Login" && Object.keys(user).length === 0) {
        next({ name: 'Login' });
    }
    else if (to.name === "Login" && Object.keys(user).length !== 0) {
        next({ name: 'Home' });
    }
    else {
        next();
    }
})

export default router;
