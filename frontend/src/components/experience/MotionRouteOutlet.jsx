import React, { useLayoutEffect, useRef } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import { gsap } from 'gsap';

export default function MotionRouteOutlet() {
  const location = useLocation();
  const ref = useRef(null);

  useLayoutEffect(() => {
    const root = ref.current;
    if (!root || window.matchMedia('(prefers-reduced-motion: reduce)').matches) return;

    const target = root.firstElementChild;
    if (!target) return;

    const ctx = gsap.context(() => {
      gsap.fromTo(target, { autoAlpha: 0, y: 10 }, {
        autoAlpha: 1, y: 0, duration: 0.45, ease: 'power3.out',
        clearProps: 'transform,opacity,visibility',
      });

      const children = target.querySelectorAll(
        ':scope > [data-lifeos-reveal], :scope > header, :scope > section, :scope > article'
      );
      if (children.length) {
        gsap.fromTo(children, { autoAlpha: 0, y: 8 }, {
          autoAlpha: 1, y: 0, duration: 0.32, stagger: 0.05,
          ease: 'power2.out', delay: 0.04,
          clearProps: 'transform,opacity,visibility',
        });
      }
    }, root);

    return () => ctx.revert();
  }, [location.pathname, location.search]);

  return <div ref={ref} className="lifeos-route-motion-host"><Outlet /></div>;
}
