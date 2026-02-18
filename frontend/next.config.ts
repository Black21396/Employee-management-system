import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // CRITICAL: enables standalone mode — drastically reduces image size (node_modules are not included in the image, only the necessary files)
  output: "standalone",
};

export default nextConfig;
