<template>
    <div>
        <router-view></router-view>
    </div>
</template>

<script>
export default {
    watch: {
        $route: {
            immediate: true,
            handler(to) {
                document.title = this.$i18n.t("Admin") + " | " + this.$i18n.t(to.meta.title);
            },
        },
    },
};
</script>
