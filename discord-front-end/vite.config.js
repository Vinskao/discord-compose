import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import { resolve } from "path";

// https://vitejs.dev/config/
export default defineConfig({
  base: "./",
  plugins: [vue()],
  server: {
    host: true,
    port: 8090,
    open: false,
    fs: {
      strict: false,
    },
  },
  resolve: {
    alias: {
      "@": resolve(__dirname, "src"),
      "@images": resolve(__dirname, "src/assets/images"),
      "@public": resolve(__dirname, "public"),
    },
  },
});
