<template>
    <a v-if="as == 'a'" :href="href" class="cursor-pointer block px-4 py-2 text-sm leading-5 text-gray-700 hover:bg-gray-100 focus:outline-none focus:bg-gray-100 transition duration-150 ease-in-out">
        <slot></slot>
    </a>
    <router-link v-else :to="href" class="block px-4 py-2 text-sm leading-5 text-gray-700 hover:bg-gray-100 focus:outline-none focus:bg-gray-100 transition duration-150 ease-in-out">
        <slot></slot>
    </router-link>
</template>

<script>
export default {
    props: ["href", "as"],
};
</script>
