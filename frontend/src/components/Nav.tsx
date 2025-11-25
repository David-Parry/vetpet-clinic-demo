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

/**
 * Validates that a URL is safe to use for fetching profile images.
 * 
 * Security rationale:
 * - Prevents DOM-based XSS by ensuring only trusted URLs are fetched
 * - Validates URL scheme (https: for production, http: allowed for dev)
 * - Validates URL host matches current origin or trusted backend hosts
 * - Fails closed: returns false for any invalid or untrusted URL
 * 
 * @param url - The URL to validate
 * @returns true if the URL is trusted and safe to fetch, false otherwise
 */
function isTrustedImageUrl(url: string): boolean {
  try {
    // Parse the URL relative to the current origin to handle relative URLs correctly
    const parsedUrl = new URL(url, window.location.origin);
    
    // Allow https: for production, http: for local development
    const allowedProtocols = ['https:', 'http:'];
    if (!allowedProtocols.includes(parsedUrl.protocol)) {
      console.warn(`Untrusted URL protocol: ${parsedUrl.protocol}`);
      return false;
    }
    
    // Only allow URLs from the current host (same-origin policy)
    // This prevents fetching from arbitrary external domains
    if (parsedUrl.host !== window.location.host) {
      console.warn(`Untrusted URL host: ${parsedUrl.host} (expected: ${window.location.host})`);
      return false;
    }
    
    return true;
  } catch (error) {
    // Invalid URL format
    console.warn(`Invalid URL format: ${url}`, error);
    return false;
  }
}

type ProfileImageProps = {
  url: string;
  alt: string;
};
function ProfileImage({ url, alt }: ProfileImageProps) {
  const [token] = useAuthToken();
  const [imageData, setImageData] = useState<string | null>(null);

  useEffect(() => {
    if (!token) {
      setImageData(null);
      return;
    }
    
    // Security check: Validate URL before fetching to prevent DOM-based XSS
    if (!isTrustedImageUrl(url)) {
      setImageData(null);
      return;
    }
    
    const reader = new FileReader();

    fetch(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => res.blob())
      .then(
        (blob) =>
          new Promise((resolve, reject) => {
            reader.onload = resolve;
            reader.onerror = reject;
            reader.readAsDataURL(blob);
          }),
      )
      .then((_) => reader.result)
      .then((imageData) =>
        typeof imageData === "string"
          ? setImageData(imageData)
          : setImageData(null),
      );
  }, [token, url]);

  if (imageData) {
    return <img src={imageData} className="h-8 w-8 rounded-full" alt={alt} />;
  }

  return null;
}
