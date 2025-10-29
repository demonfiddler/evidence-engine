"use client";

import { cn } from "@/lib/utils";
import { Star } from "lucide-react";
import * as React from "react";
import { CSSProperties, memo, useCallback, useState } from "react";
import { Button } from "../ui/button";

export interface StarRatingBasicProps {
  id?: string
  className?: string
  ariaLabel?: string
  ariaLabelledby?: string
  value: number
  onChange?: (value: number) => void
  iconSize?: number
  maxStars?: number
  disabled?: boolean
  color?: string
}

const StarIcon = memo(
  ({
    iconSize,
    index,
    isInteractive,
    // onClick,
    // onMouseEnter,
    style,
  }: {
    index: number;
    style: CSSProperties;
    iconSize: number;
    // onClick: () => void;
    // onMouseEnter: () => void;
    isInteractive: boolean;
  }) => (
    <Star
      key={index}
      size={iconSize}
      fill={style.fill}
      color={style.color}
      // onClick={onClick}
      // onMouseEnter={onMouseEnter}
      className={cn(
        "transition-colors duration-200",
        isInteractive && "cursor-pointer hover:scale-110"
      )}
      style={style}
    />
  )
);
StarIcon.displayName = "StarIcon";

const StarRating_Basic = ({
  className,
  id,
  ariaLabel,
  ariaLabelledby,
  color = "#e4c616",
  iconSize = 24,
  maxStars = 5,
  onChange,
  disabled = false,
  value,
}: StarRatingBasicProps) => {
  const [hoverRating, setHoverRating] = useState<number | null>(null);

  const handleStarClick = useCallback(
    (index: number) => {
      if (disabled || !onChange) return;
      const newRating = index + 1;
      onChange(newRating === value ? 0 : newRating);
    },
    [disabled, value, onChange]
  );

  const handleStarHover = useCallback(
    (index: number) => {
      if (!disabled) {
        setHoverRating(index + 1);
      }
    },
    [disabled]
  );

  const handleMouseLeave = useCallback(() => {
    if (!disabled) {
      setHoverRating(null);
    }
  }, [disabled]);

  const getStarStyle = useCallback(
    (index: number) => {
      const ratingToUse =
        !disabled && hoverRating !== null ? hoverRating : value;
      return {
        color: ratingToUse > index ? color : "gray",
        fill: ratingToUse > index ? color : "transparent",
      } as CSSProperties;
    },
    [disabled, hoverRating, value, color]
  );

  const stars = React.useMemo(() => {
    return Array.from({ length: maxStars }).map((_, index) => {
      const style = getStarStyle(index);
      return (
        <Button
          key={index}
          type="button"
          variant="ghost"
          role="radio"
          disabled={disabled}
          className="p-0 has-[>svg]:p-0"
          onClick={() => handleStarClick(index)}
          onMouseEnter={() => handleStarHover(index)}
          title={`Click to toggle star rating of ${index + 1}`}
        >
          <StarIcon
            index={index}
            style={style}
            iconSize={iconSize}
            isInteractive={!disabled}
          />
        </Button>
      );
    });
  }, [
    maxStars,
    getStarStyle,
    iconSize,
    handleStarClick,
    handleStarHover,
    disabled,
  ]);

  return (
    <div
      id={id}
      aria-label={ariaLabel}
      aria-labelledby={ariaLabelledby}
      role="radiogroup"
      className={cn("flex items-center gap-x-0.5", className)}
      onMouseLeave={handleMouseLeave}
    >
      {stars}
    </div>
  );
};

export default StarRating_Basic;
