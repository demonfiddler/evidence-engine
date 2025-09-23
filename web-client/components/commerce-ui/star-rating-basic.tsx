"use client";

import { cn } from "@/lib/utils";
import { Star } from "lucide-react";
import * as React from "react";
import { CSSProperties, memo, useCallback, useState } from "react";

export interface StarRatingBasicProps {
  value: number;
  onChange?: (value: number) => void;
  className?: string;
  iconSize?: number;
  maxStars?: number;
  readOnly?: boolean;
  color?: string;
}

const StarIcon = memo(
  ({
    iconSize,
    index,
    isInteractive,
    onClick,
    onMouseEnter,
    style,
  }: {
    index: number;
    style: CSSProperties;
    iconSize: number;
    onClick: () => void;
    onMouseEnter: () => void;
    isInteractive: boolean;
  }) => (
    <Star
      key={index}
      size={iconSize}
      fill={style.fill}
      color={style.color}
      onClick={onClick}
      onMouseEnter={onMouseEnter}
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
  color = "#e4c616",
  iconSize = 24,
  maxStars = 5,
  onChange,
  readOnly = false,
  value,
}: StarRatingBasicProps) => {
  const [hoverRating, setHoverRating] = useState<number | null>(null);

  const handleStarClick = useCallback(
    (index: number) => {
      if (readOnly || !onChange) return;
      const newRating = index + 1;
      onChange(newRating === value ? 0 : newRating);
    },
    [readOnly, value, onChange]
  );

  const handleStarHover = useCallback(
    (index: number) => {
      if (!readOnly) {
        setHoverRating(index + 1);
      }
    },
    [readOnly]
  );

  const handleMouseLeave = useCallback(() => {
    if (!readOnly) {
      setHoverRating(null);
    }
  }, [readOnly]);

  const getStarStyle = useCallback(
    (index: number) => {
      const ratingToUse =
        !readOnly && hoverRating !== null ? hoverRating : value;
      return {
        color: ratingToUse > index ? color : "gray",
        fill: ratingToUse > index ? color : "transparent",
      } as CSSProperties;
    },
    [readOnly, hoverRating, value, color]
  );

  const stars = React.useMemo(() => {
    return Array.from({ length: maxStars }).map((_, index) => {
      const style = getStarStyle(index);
      return (
        <StarIcon
          key={index}
          index={index}
          style={style}
          iconSize={iconSize}
          onClick={() => handleStarClick(index)}
          onMouseEnter={() => handleStarHover(index)}
          isInteractive={!readOnly}
        />
      );
    });
  }, [
    maxStars,
    getStarStyle,
    iconSize,
    handleStarClick,
    handleStarHover,
    readOnly,
  ]);

  return (
    <div
      className={cn("flex items-center gap-x-0.5", className)}
      onMouseLeave={handleMouseLeave}
    >
      {stars}
    </div>
  );
};

export default StarRating_Basic;
