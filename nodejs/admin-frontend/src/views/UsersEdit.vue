<template>
    <div>
        <!-- Form edit -->
        <div class="p-4 text-3xl mr-auto">
            {{ $t('Edit user') }}
        </div>
        <!-- Loading component -->
        <div v-if="isLoading" class="relative w-full h-96">
            <loading :active.sync="isLoading" :is-full-page="false" :height="40" :width="40" :color="'#007BFF'" :loader="'dots'"></loading>
        </div>
        <div v-if="!isLoading" class="w-full sm:max-w-4xl mt-6 px-6 py-4 bg-white sm:rounded-lg">
            <!-- Validation Errors -->
            <div class="mb-4" v-if="error">
                <div class="font-medium text-red-600">
                    {{ $t('Whoops! Something went wrong.') }}
                </div>

                <div class="mt-3 list-disc list-inside text-sm text-red-600">
                    {{ $t(error) }}
                </div>
            </div>

            <form @submit.prevent="updateUser">
                <!-- User ID -->
                <div>
                    <label for="userId" class="block font-medium text-sm text-gray-700">{{ $t('ID') }}</label>
                    <input class="block mt-1 w-full rounded-md shadow-sm bg-gray-300 border-gray-300 focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50" type="text" id="userId" name="userId" v-model="form.userId" required readonly />
                </div>

                <!-- Name -->
                <div class="mt-4">
                    <label for="name" class="block font-medium text-sm text-gray-700">{{ $t('Name') }}</label>
                    <input class="block mt-1 w-full rounded-md shadow-sm border-gray-300 focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50" type="text" id="name" name="name" v-model="form.name" required />
                </div>

                <!-- Email -->
                <div class="mt-4">
                    <label for="email" class="block font-medium text-sm text-gray-700">{{ $t('Email') }}</label>
                    <input class="block mt-1 w-full rounded-md shadow-sm border-gray-300 focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50" type="email" id="email" name="email" v-model="form.email" required />
                </div>

                <!-- Gender -->
                <div class="mt-4">
                    <label for="gender" class="block font-medium text-sm text-gray-700">{{ $t('Gender') }}</label>
                    <select class="block mt-1 w-full rounded-md shadow-sm border-gray-300 focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50" id="gender" name="gender" v-model="form.gender" required>
                        <option :value="'Nam'">{{ $t("Male") }}</option>
                        <option :value="'Nữ'">{{ $t("Female") }}</option>
                        <option :value="'Khác'">{{ $t("Other") }}</option>
                    </select>
                </div>

                <!-- Date -->
                <div class="mt-4">
                    <label for="date" class="block font-medium text-sm text-gray-700">{{ $t('Date') }}</label>
                    <input class="block mt-1 w-full rounded-md shadow-sm border-gray-300 focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50" type="date" id="date" name="date" v-model="form.date" required />
                </div>

                <div class="mt-8 flex-row">
                    <button :class="{ 'opacity-25': isFormLoading }" :disabled="isFormLoading" class="ml-3 inline-flex items-center px-4 py-2 bg-blue-500 border border-transparent rounded-md font-semibold text-xs text-white uppercase tracking-widest hover:bg-blue-700 active:bg-blue-900 focus:outline-none focus:border-blue-900 focus:ring ring-blue-300 disabled:opacity-25 transition ease-in-out duration-150" type="submit">
                        {{ $t('Update') }}
                    </button>
                    <router-link :to="{ name: 'UsersIndex' }" class="ml-3 inline-flex items-center px-4 py-2 bg-blue-500 border border-transparent rounded-md font-semibold text-xs text-white uppercase tracking-widest hover:bg-blue-700 active:bg-blue-900 focus:outline-none focus:border-blue-900 focus:ring ring-blue-300 disabled:opacity-25 transition ease-in-out duration-150">
                        {{ $t('Cancel') }}
                    </router-link>
                </div>
            </form>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            user: {},
            form: {
                userId: Number,
                name: "",
                email: "",
                gender: "",
                date: ""
            },
            error: "",
            isLoading: true,
            isFormLoading: false,
        };
    },
    async created() {
        this.isLoading = true;
        const dataObj = {
            userId: this.$route.params.id,
        };
        const url = "api/admin/user/edit";
        const res = await this.callApi("post", url, dataObj);
        if (res.status === 200) {
            this.user = res.data.results[0];
            this.isLoading = false;
            // init data form
            this.form.userId = this.user.id;
            this.form.name = this.user.name;
            this.form.email = this.user.email;
            this.form.gender = this.user.gender;
            this.form.date = new Date(this.user.date.slice(6) + "/" + this.user.date.slice(3, 5) + "/" + this.user.date.slice(0, 2)).toISOString().slice(0,10);
        } else {
            alert(this.$i18n.t("Get data error. Please reload page !"));
        }
    },
    methods: {
        async updateUser() {
            this.error = "";
            this.isFormLoading = true;
            const url = "api/user/update";
            const dataObj = {
                userId: this.form.userId,
                name: this.form.name,
                email: this.form.email,
                gender: this.form.gender,
                date: this.formatDateTime(this.form.date)
            };
            const res = await this.callApi("post", url, dataObj);
            if (res.status === 200) {
                this.$router.push({ name: "UsersIndex" });
            } else if (res.status === 400) {
                this.error = "Error, please try again.";
            } else {
                alert(this.$i18n.t("Post data error. Please try again !"));
            }
            this.isFormLoading = false;
        },
    },
};
</script>
