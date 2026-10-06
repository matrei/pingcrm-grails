import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import inertia from '@inertiajs/vite'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'

// https://vitejs.dev/config/
export default defineConfig(({ command }) => ({
  base: command === 'serve' ? '' : '/static/dist/',
  publicDir: false,
  build: {
    manifest: true,
    outDir: 'src/main/resources/public/dist',
    assetsDir: 'js',
    rollupOptions: {
      input: 'src/main/javascript/main.js',
    }
  },

  plugins: [
      // Turns the pages shorthand of createInertiaApp into a page resolver.
      // Its SSR support is for Laravel; grails-inertia runs the built SSR bundle itself.
      inertia({ ssr: false }),
      vue(),
      tailwindcss(),
      Components({
        dirs: ['src/main/javascript/Shared'],
      }),
      AutoImport({
        imports: ['vue']
      })
  ],

  ssr: {
    // Bundles the dependencies into the SSR bundle so that it runs without node_modules
    noExternal: true,
  },

  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src/main/javascript', import.meta.url))
    }
  },

  server: {
    // Needed for changes to picked up when running in WSL on Windows
    watch: {
      usePolling: true
    }
  }
}))