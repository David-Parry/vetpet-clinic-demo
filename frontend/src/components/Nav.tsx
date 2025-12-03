import * as React from "react";
import { NavLink as RouterNavLink } from "react-router-dom";
import SpringLogo from "@/assets/Spring_Framework_Logo_2018.svg";
import Link from "./Link";
import { useAuthToken } from "@/login/AuthTokenProvider";
import { useLogout } from "@/use-logout";
import { useCurrentUser } from "@/use-current-user-fullname";
import clsx from "clsx";
import { useEffect, useState } from "react";

function NavLogo() {
  return (
    <div className="flex items-center">
      <div className="flex-shrink-0">
        <Link to="/">
          <img className="h-12" src={SpringLogo} alt="Spring Framework logo" />
        </Link>
      </div>
    </div>
  );
}

type NavBarProps = {
  nav?: React.ReactElement;
  mobileMenu?: React.ReactElement;
};
export function NavBar({ nav, mobileMenu }: NavBarProps) {
  return (
    <nav className="bg-spr-green-light">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="flex h-16 items-center justify-between">
          <NavLogo />
          {nav}
        </div>
      </div>
      {mobileMenu}
    </nav>
  );
}

type NavLinkProps = {
  to: string;
  children: React.ReactNode;
  exact?: boolean;
};
function NavLink({ children, to }: NavLinkProps) {
  return (
    <RouterNavLink
      to={to}
      className={({ isActive }) =>
        clsx(
          "h-12  px-4 py-2 text-sm font-bold uppercase text-spr-black hover:border-spr-green  hover:bg-spr-white",
          isActive || "border-t-4 border-spr-green-light",
          isActive && "border-t-4  border-spr-green  bg-spr-white",
        )
      }
    >
      {children}
    </RouterNavLink>
  );
}

export function DefaultNavBar() {
  const handleSignOut = useLogout();
  const { username, fullname } = useCurrentUser();
  const [profileMenuOpen, setProfileMenuOpen] = React.useState(false);

  return (
    <NavBar
      nav={
        <div className="hidden md:block">
          <div className="ml-10 flex space-x-4">
            <NavLink to="/" exact>
              Home
            </NavLink>
            <NavLink to="/owners">Owners</NavLink>
            <NavLink to="/vets">Veterinarians</NavLink>
            <div className="ml-4 flex items-center md:ml-6">
              {!!username && (
                <div className="relative ml-3 ">
                  <div className="">
                    <button
                      onClick={() => setProfileMenuOpen(!profileMenuOpen)}
                      className="focus:ring-white flex max-w-xs items-center rounded-full text-sm focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-spr-gray-dark"
                      id="user-menu"
                      aria-haspopup="true"
                    >
                      <span className="sr-only">Open user menu</span>
                      <ProfileImage
                        url={`/images/${username}.png`}
                        alt={`Profile image of ${username}`}
                      />
                    </button>
                  </div>

                  {profileMenuOpen && (
                    <div
                      className="bg-white absolute right-0 mt-2 w-48 origin-top-right rounded-md bg-spr-white py-1 shadow-lg ring-1 ring-spr-gray-dark ring-opacity-5"
                      role="menu"
                      aria-orientation="vertical"
                      aria-labelledby="user-menu"
                    >
                      <span className="block cursor-pointer border-b px-4 py-2 text-sm text-spr-black">
                        Signed in as <b>{fullname}</b>
                      </span>

                      <button
                        className="block w-full px-4 py-2 text-left text-sm text-spr-black hover:bg-gray-100"
                        role="menuitem"
                        onClick={handleSignOut}
                      >
                        Sign out
                      </button>
                    </div>
                  )}
                </div>
              )}
            </div>
          </div>
        </div>
      }
      mobileMenu={
        <div className="hidden md:hidden">
          <div className="space-y-1 px-2 pb-3 pt-2 sm:px-3">
            <a
              href="#"
              className="text-white block rounded-md bg-gray-900 px-3 py-2 text-base font-medium"
            >
              Home
            </a>

            <a
              href="#"
              className="hover:text-white block rounded-md px-3 py-2 text-base font-medium text-gray-300 hover:bg-gray-700"
            >
              Owners
            </a>

            <a
              href="#"
              className="hover:text-white block rounded-md px-3 py-2 text-base font-medium text-gray-300 hover:bg-gray-700"
            >
              Veterinarians
            </a>

            <a
              href="#"
              className="hover:text-white block rounded-md px-3 py-2 text-base font-medium text-gray-300 hover:bg-gray-700"
            >
              Specialities
            </a>

            <a
              href="#"
              className="hover:text-white block rounded-md px-3 py-2 text-base font-medium text-gray-300 hover:bg-gray-700"
            >
              Reports
            </a>
          </div>
        </div>
      }
    />
  );
}

type ProfileImageProps = {
  url: string;
  alt: string;
};

/**
 * Helper function to validate if a blob is a safe image type
 * Prevents DOM-based XSS by ensuring only image MIME types are rendered
 */
function isSafeImage(blob: Blob): boolean {
  return blob.type.startsWith("image/");
}

/**
 * ProfileImage component with XSS protection
 * - Validates MIME type before rendering
 * - Uses object URLs instead of Base64 for better security
 * - Properly cleans up object URLs to prevent memory leaks
 * - Provides fallback avatar for invalid content
 */
function ProfileImage({ url, alt }: ProfileImageProps) {
  const [token] = useAuthToken();
  const [imageUrl, setImageUrl] = useState<string | null>(null);
  const [showFallback, setShowFallback] = useState(false);

  useEffect(() => {
    // Reset state when token or url changes
    if (!token) {
      setImageUrl(null);
      setShowFallback(false);
      return;
    }

    let objectUrl: string | null = null;

    // Fetch and validate image
    const loadImage = async () => {
      try {
        const response = await fetch(url, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          console.warn(`Failed to fetch profile image: ${response.status}`);
          setShowFallback(true);
          return;
        }

        const blob = await response.blob();

        // CRITICAL: Validate MIME type to prevent XSS
        if (!isSafeImage(blob)) {
          console.error(
            `Invalid image MIME type: ${blob.type}. Only image/* types are allowed.`,
          );
          setShowFallback(true);
          return;
        }

        // Create safe object URL
        objectUrl = URL.createObjectURL(blob);
        setImageUrl(objectUrl);
        setShowFallback(false);
      } catch (error) {
        console.error("Error loading profile image:", error);
        setShowFallback(true);
      }
    };

    loadImage();

    // Cleanup: Revoke object URL to prevent memory leaks
    return () => {
      if (objectUrl) {
        URL.revokeObjectURL(objectUrl);
      }
    };
  }, [token, url]);

  // Render validated image
  if (imageUrl && !showFallback) {
    return <img src={imageUrl} className="h-8 w-8 rounded-full" alt={alt} />;
  }

  // Fallback avatar with user initials
  if (showFallback) {
    // Extract initials from alt text (e.g., "Profile image of john" -> "J")
    const initials = alt
      .split(" ")
      .slice(-1)[0]
      ?.charAt(0)
      .toUpperCase() || "?";
    
    return (
      <div
        className="flex h-8 w-8 items-center justify-center rounded-full bg-spr-green text-white font-bold text-sm"
        title={alt}
      >
        {initials}
      </div>
    );
  }

  // Loading state (no image yet)
  return null;
}
