package com.home.croaton.audiotravel.domain;

import com.google.android.gms.maps.model.LatLng;

public class Point
{
    public Integer Number;
    public LatLng Position;

    public Point(int number, LatLng position)
    {
        Number = number;
        Position = position;
    }

    @Override
    public int hashCode()
    {
        return Number/*Position.hashCode()*/;
    }
}
