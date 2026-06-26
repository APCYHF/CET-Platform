import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { resolve } from 'path';

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  build: {
    rollupOptions: {
      input: {
        main: resolve(__dirname, 'index.html'),
        admin: resolve(__dirname, 'admin.html')
      },
      output: {
        manualChunks(id) {
          if (id.includes('node_modules/vant')) return 'vant';
          if (id.includes('node_modules/element-plus')) return 'element';
          if (id.includes('node_modules/echarts') || id.includes('node_modules/vue-echarts')) return 'charts';
          if (id.includes('node_modules')) return 'vendor';
        }
      }
    },
    chunkSizeWarningLimit: 500,
    cssCodeSplit: true,
    minify: 'esbuild',
    sourcemap: false
  }
});
