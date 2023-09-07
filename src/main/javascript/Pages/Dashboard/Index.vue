<script>
import layout from '@/Shared/Layout.vue'
export default { layout }
</script>

<template>
  <inertia-head title="Dashboard" />
  <h1 class="mb-8 font-bold text-3xl">Dashboard</h1>
  <p class="mb-8 leading-normal">
    Hey there! Welcome to Ping CRM, a demo app designed to help illustrate how
    <a class="text-indigo-500 underline hover:text-orange-600" href="https://inertiajs.com">Inertia.js</a> works
    with <a class="text-indigo-500 underline hover:text-orange-600" href="https://grails.org">Grails</a>.
  </p>
  <div class="leading-[50px]">
    <inertia-link href="/test-500-error" class="btn-indigo">Trigger 500 Server Error</inertia-link>&nbsp;
    <inertia-link href="/static/test-404-error" class="btn-indigo">Trigger 404 Not Found</inertia-link>&nbsp;
    <inertia-link href="/hello" class="btn-indigo">Trigger 403 Forbidden</inertia-link>
  </div>
</template>