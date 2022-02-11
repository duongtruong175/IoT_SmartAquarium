<template>
    <div>
        <!-- Form edit -->
        <div class="p-4 text-3xl mr-auto">
            {{ $t('Edit device') }}
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

            <form @submit.prevent="updateDevice">
                <!-- ID -->
                <div>
                    <label for="id" class="block font-medium text-sm text-gray-700">{{ $t('ID') }}</label>
                    <input class="block mt-1 w-full rounded-md shadow-sm bg-gray-300 border-gray-300 focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50" type="text" id="id" name="id" v-model="form.id" required readonly />
                </div>

                <!-- Device Id -->
                <div class="mt-4">
                    <label for="deviceId" class="block font-medium text-sm text-gray-700">{{ $t('Device Id') }}</label>
                    <input class="block mt-1 w-full rounded-md shadow-sm border-gray-300 focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50" type="text" id="deviceId" name="deviceId" v-model="form.deviceId" required />
                </div>

                <!-- Device Type -->
                <div class="mt-4">
                    <label for="deviceType" class="block font-medium text-sm text-gray-700">{{ $t('Device Type') }}</label>
                    <input class="block mt-1 w-full rounded-md shadow-sm border-gray-300 focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50" type="text" id="deviceType" name="deviceType" v-model="form.deviceType" required />
                </div>

                <div class="mt-8 flex-row">
                    <button :class="{ 'opacity-25': isFormLoading }" :disabled="isFormLoading" class="ml-3 inline-flex items-center px-4 py-2 bg-blue-500 border border-transparent rounded-md font-semibold text-xs text-white uppercase tracking-widest hover:bg-blue-700 active:bg-blue-900 focus:outline-none focus:border-blue-900 focus:ring ring-blue-300 disabled:opacity-25 transition ease-in-out duration-150" type="submit">
                        {{ $t('Update') }}
                    </button>
                    <router-link :to="{ name: 'DevicesIndex' }" class="ml-3 inline-flex items-center px-4 py-2 bg-blue-500 border border-transparent rounded-md font-semibold text-xs text-white uppercase tracking-widest hover:bg-blue-700 active:bg-blue-900 focus:outline-none focus:border-blue-900 focus:ring ring-blue-300 disabled:opacity-25 transition ease-in-out duration-150">
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
            device: {},
            form: {
                id: Number,
                deviceType: "",
                deviceId: ""
            },
            error: "",
            isLoading: true,
            isFormLoading: false,
        };
    },
    async created() {
        this.isLoading = true;
        const dataObj = {
            id: this.$route.params.id,
        };
        const url = "api/admin/aquarium/edit";
        const res = await this.callApi("post", url, dataObj);
        if (res.status === 200) {
            this.device = res.data.results[0];
            this.isLoading = false;
            // init data form
            this.form.id = this.device.id;
            this.form.deviceType = this.device.device_type;
            this.form.deviceId = this.device.device_id;
        } else {
            alert(this.$i18n.t("Get data error. Please reload page !"));
        }
    },
    methods: {
        async updateDevice() {
            this.error = "";
            this.isFormLoading = true;
            const url = "api/admin/aquarium/update";
            const dataObj = {
                id: this.form.id,
                deviceId: this.form.deviceId,
                deviceType: this.form.deviceType
            };
            const res = await this.callApi("post", url, dataObj);
            if (res.status === 200) {
                this.$router.push({ name: "DevicesIndex" });
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
