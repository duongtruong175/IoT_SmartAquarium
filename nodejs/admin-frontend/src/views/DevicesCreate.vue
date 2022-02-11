<template>
    <div>
        <!-- Form Create new -->
        <div class="p-4 text-3xl mr-auto">
            {{ $t('Add new device') }}
        </div>
        <div class="w-full sm:max-w-md mt-6 px-6 py-4 bg-white sm:rounded-lg">
            <!-- Validation Errors -->
            <div class="mb-4" v-if="error">
                <div class="font-medium text-red-600">
                    {{ $t('Whoops! Something went wrong.') }}
                </div>

                <div class="mt-3 list-disc list-inside text-sm text-red-600">
                    {{ $t(error) }}
                </div>
            </div>

            <form @submit.prevent="addDevice">
                <!-- Device Id -->
                <div>
                    <label for="deviceId" class="block font-medium text-sm text-gray-700">{{ $t('Device Id') }}</label>
                    <input class="block mt-1 w-full rounded-md shadow-sm border-gray-300 focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50" type="text" id="deviceId" name="deviceId" v-model="form.deviceId" required autofocus />
                </div>

                <!-- Device Type -->
                <div class="mt-4">
                    <label for="deviceType" class="block font-medium text-sm text-gray-700">{{ $t('Device Type') }}</label>
                    <input class="block mt-1 w-full rounded-md shadow-sm border-gray-300 focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50" type="text" id="deviceType" name="deviceType" v-model="form.deviceType" required />
                </div>

                <div class="mt-8 flex-row">
                    <button :class="{ 'opacity-25': isFormLoading }" :disabled="isFormLoading" class="ml-3 inline-flex items-center px-4 py-2 bg-blue-500 border border-transparent rounded-md font-semibold text-xs text-white uppercase tracking-widest hover:bg-blue-700 active:bg-blue-900 focus:outline-none focus:border-blue-900 focus:ring ring-blue-300 disabled:opacity-25 transition ease-in-out duration-150" type="submit">
                        {{ $t('Add') }}
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
            form: {
                deviceId: "",
                deviceType: "",
            },
            error: "",
            isFormLoading: false,
        };
    },
    methods: {
        async addDevice() {
            this.error = "";
            this.isFormLoading = true;
            const url = "api/aquarium/create";
            const dataObj = {
                deviceId: this.form.deviceId,
                deviceType: this.form.deviceType
            };
            const res = await this.callApi("post", url, dataObj);
            if (res.status === 201) {
                this.$router.push({ name: "DevicesIndex" });
            } else if (res.status === 400) {
                this.error = "Device Id is already taken or error.";
            } else {
                alert(this.$i18n.t("Post data error. Please try again !"));
            }
            this.isFormLoading = false;
        },
    },
};
</script>
