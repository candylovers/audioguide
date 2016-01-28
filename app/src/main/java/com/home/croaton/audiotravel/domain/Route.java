package com.home.croaton.audiotravel.domain;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Hashtable;

public class Route
{
    private ArrayList<AudioPoint> _audioPoints;
    private ArrayList<Point> _geoPoints;
    private Hashtable<Point, ArrayList<Integer>> _pointTrackMapper;

    public Route()
    {
        _pointTrackMapper = new Hashtable<>();
        _geoPoints = new ArrayList<>();
        _audioPoints = new ArrayList<>();
    }

    public void addGeoPoint(LatLng position)
    {
        _geoPoints.add(new Point(_geoPoints.size() + 1, position));
    }

    public void addGeoPoint(int number, LatLng position)
    {
        _geoPoints.add(new Point(number, position));
    }

    public void addAudioPoint(AudioPoint audioPoint)
    {
        //audioPoint.Number = _audioPoints.size();
        _audioPoints.add(audioPoint);
    }

    public Point getGeoPoint(int index)
    {
        return _geoPoints.get(index);
    }

    public ArrayList<Point> geoPoints()
    {
        return (ArrayList<Point>)_geoPoints.clone();
    }

    public ArrayList<AudioPoint> audioPoints()
    {
        return (ArrayList<AudioPoint>)_audioPoints.clone();
    }

    public void markAudioPointDone(int pointNumber)
    {
        _audioPoints.get(pointNumber).Done = true;
    }

    public ArrayList<Integer> getAudiosForPoint(AudioPoint closestPoint)
    {
        return _pointTrackMapper.get(closestPoint);
    }

    public void addAudioTrack(AudioPoint point, int fileId) {
        if (_pointTrackMapper.containsKey(point))
        {
            _pointTrackMapper.get(point).add(fileId);
            return;
        }

        ArrayList<Integer> audioFilesIds = new ArrayList<>();
        audioFilesIds.add(fileId);
        _pointTrackMapper.put(point, audioFilesIds);
    }
}