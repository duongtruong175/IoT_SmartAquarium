<template>
    <div>
        <!-- Header + Contain dashboard -->
        <div class="text-3xl p-4 mb-8">
            {{ $t('Dashboard') }}
        </div>
        <!-- Loading component -->
        <div v-if="isLoading" class="relative w-full h-96">
            <loading :active.sync="isLoading" :is-full-page="false" :height="40" :width="40" :color="'#007BFF'" :loader="'dots'"></loading>
        </div>
        <div v-if="!isLoading" class="flex flex-wrap">
            <div class="w-1/2 sm:w-1/3 lg:w-1/4 px-2">
                <div class="bg-blue-400 relative rounded mb-4">
                    <div class="items-center text-center sm:text-left sm:block text-white p-6">
                        <div class="font-extrabold text-3xl">
                            {{ total_users }}
                        </div>
                        <div class="text-sm mt-2 font-medium">
                            {{ $t('Users Registrations') }}
                        </div>
                    </div>
                    <div class="hidden sm:block absolute top-1/3 right-4">
                        <user-icon class="w-8 h-8 transition duration-300 transform hover:scale-125" style="color: rgba(0, 0, 0, 0.15)"></user-icon>
                    </div>
                    <a class="flex justify-center cursor-pointer text-center p-1 w-full" style="background-color: rgba(0,0,0,.1)">
                        <span class="text-white text-sm mr-1">{{ $t('More info') }}</span>
                        <arrow-circle-right-icon class="h-5 w-5 text-white inline-block"></arrow-circle-right-icon>
                    </a>
                </div>
            </div>
            <div class="w-1/2 sm:w-1/3 lg:w-1/4 px-2">
                <div class="bg-green-400 relative rounded mb-4">
                    <div class="items-center text-center sm:text-left sm:block text-white p-6">
                        <div class="font-extrabold text-3xl">
                            {{ total_devices }}
                        </div>
                        <div class="text-sm mt-2 font-medium">
                            {{ $t('Devices Created') }}
                        </div>
                    </div>
                    <div class="hidden sm:block absolute top-1/3 right-4">
                        <device-icon class="w-8 h-8 transition duration-300 transform hover:scale-125" style="color: rgba(0, 0, 0, 0.15)"></device-icon>
                    </div>
                    <a class="flex justify-center cursor-pointer text-center p-1 w-full" style="background-color: rgba(0,0,0,.1)">
                        <span class="text-white text-sm mr-1">{{ $t('More info') }}</span>
                        <arrow-circle-right-icon class="h-5 w-5 text-white inline-block"></arrow-circle-right-icon>
                    </a>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import ArrowCircleRightIcon from '../components/ArrowCircleRightIcon.vue'
import UserIcon from '../components/UserIcon.vue'
import DeviceIcon from '../components/DeviceIcon.vue'

export default {
    components: {
        ArrowCircleRightIcon,
        UserIcon,
        DeviceIcon
    },
    data() {
        return {
            total_users: Number,
            total_devices: Number,
            isLoading: true,
        };
    },
    async created() {
        this.isLoading = true;
        const url = "api/admin/dashboard";
        const res = await this.callApi("get", url);
        if (res.status === 200) {
            this.total_users = res.data.results[0].total_users;
            this.total_devices = res.data.results[0].total_devices;
            this.isLoading = false;
        } else {
            alert(this.$i18n.t("Get data error. Please reload page !"));
        }
    },
};
</script>
