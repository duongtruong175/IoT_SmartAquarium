<template>
    <div>
        <div class="min-h-screen">
            <!-- Form admin login -->
            <div class="min-h-screen flex flex-col sm:justify-center items-center pt-6 sm:pt-0 bg-gray-100">
                <div class="flex items-center text-center">
                    <application-logo class="w-20 h-20 fill-current text-gray-500"></application-logo>
                    <span class="ml-4 text-2xl">{{ $t('Admin Login') }}</span>
                </div>
            
                <div class="w-full sm:mx-2 sm:max-w-md mt-6 px-6 py-4 bg-white shadow-md overflow-hidden sm:rounded-lg">
                    <!-- Validation Errors -->
                    <div class="mb-4" v-if="error">
                        <div class="font-medium text-red-600">
                            {{ $t('Whoops! Something went wrong.') }}
                        </div>

                        <div class="mt-3 list-disc list-inside text-sm text-red-600">
                            {{ $t(error) }}
                        </div>
                    </div>

                    <form @submit.prevent="login">
                        <!-- Usename -->
                        <div>
                            <label for="username" class="block font-medium text-sm text-gray-700">{{ $t('Username') }}</label>
                            <input class="rounded-md shadow-sm border-gray-300 focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50 block mt-1 w-full" type="text" id="username" name="username" v-model="form.username" required autofocus />
                        </div>

                        <!-- Password -->
                        <div class="mt-4">
                            <label for="password" class="block font-medium text-sm text-gray-700">{{ $t('Password') }}</label>
                            <input class="rounded-md shadow-sm border-gray-300 focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50 block mt-1 w-full" type="password" id="password" name="password" v-model="form.password" required />
                        </div>

                        <div class="flex items-center justify-end mt-4">
                            <button :class="{ 'opacity-25': isFormLoading }" :disabled="isFormLoading" class="inline-flex items-center px-4 py-2 bg-gray-800 border border-transparent rounded-md font-semibold text-xs text-white uppercase tracking-widest hover:bg-gray-700 active:bg-gray-900 focus:outline-none focus:border-gray-900 focus:ring ring-gray-300 disabled:opacity-25 transition ease-in-out duration-150" type="submit">
                                {{ $t('Login') }}
                            </button>
                        </div>
                    </form>
                </div>
            </div>            
        </div>
        
        <!-- Footer -->
        <footer class="mx-auto bg-gray-300 h-24 flex">
            <div class="text-center m-auto">
                <p class="text-sm">Copyright © 2021 - 2022 - Tailwind CSS. All Rights Reserved</p>
            </div>
        </footer>
    </div>
</template>

<script>
import ApplicationLogo from '../components/ApplicationLogo.vue'

export default {
    components: {
        ApplicationLogo
    },
    data() {
        return {
            form: {
                username: "",
                password: ""
            },
            error: "",
            isFormLoading: false,
        };
    },
    methods: {
        async login() {
            this.isFormLoading = true;
            const url = "api/admin/login";
            const dataObj = {
                username: this.form.username,
                password: this.form.password
            };
            const res = await this.callApi("post", url, dataObj);
            if (res.status === 200) {
                this.user = res.data.results[0];
                this.$store.commit("updateUserAuth", this.user);
                this.$router.push({ name: 'Home'});
            } else {
                this.error = "Username or password is incorrect.";
            }
            this.isFormLoading = false;
        },
    },
};
</script>
