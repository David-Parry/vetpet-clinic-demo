/**
 * Secure profile image fetching utility
 * Implements validation to prevent DOM-based XSS vulnerabilities
 */

type FetchProfileImageOptions = {
  url: string;
  token: string;
  maxBytes?: number; // Default: 1 MB
};

/**
 * Fetches a profile image and converts it to a safe data URL
 * Validates content type and size to prevent XSS attacks
 * 
 * @param options - Configuration for fetching the image
 * @returns Promise resolving to a data URL string or null if validation fails
 */
export async function fetchProfileImageAsDataUrl({
  url,
  token,
  maxBytes = 1_000_000,
}: FetchProfileImageOptions): Promise<string | null> {
  try {
    const res = await fetch(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!res.ok) {
      // Non-2xx response, reject silently
      console.warn(`Failed to fetch profile image: ${res.status} ${res.statusText}`);
      return null;
    }

    // Validate content type - only allow safe image types
    const contentType = res.headers.get("content-type") || "";
    const allowedTypes = [
      "image/png",
      "image/jpeg",
      "image/jpg",
      "image/gif",
      "image/webp",
      "image/svg+xml",
    ];
    
    if (!allowedTypes.some((t) => contentType.toLowerCase().startsWith(t))) {
      console.warn(`Invalid content type for profile image: ${contentType}`);
      return null;
    }

    // Check content length header if available
    const contentLengthHeader = res.headers.get("content-length");
    if (contentLengthHeader) {
      const contentLength = parseInt(contentLengthHeader, 10);
      if (!Number.isNaN(contentLength) && contentLength > maxBytes) {
        console.warn(`Profile image too large: ${contentLength} bytes (max: ${maxBytes})`);
        return null;
      }
    }

    const blob = await res.blob();
    
    // Verify actual blob size
    if (blob.size > maxBytes) {
      console.warn(`Profile image blob too large: ${blob.size} bytes (max: ${maxBytes})`);
      return null;
    }

    // Convert to data URL using FileReader
    return new Promise<string | null>((resolve) => {
      const reader = new FileReader();
      
      reader.onload = () => {
        const result = reader.result;
        resolve(typeof result === "string" ? result : null);
      };
      
      reader.onerror = () => {
        console.warn("FileReader error while processing profile image");
        resolve(null);
      };
      
      reader.readAsDataURL(blob);
    });
  } catch (error) {
    console.warn("Error fetching profile image:", error);
    return null;
  }
}
