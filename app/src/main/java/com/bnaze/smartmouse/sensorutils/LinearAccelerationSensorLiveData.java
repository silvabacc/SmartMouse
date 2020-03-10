package com.bnaze.smartmouse.sensorutils;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.kircherelectronics.fsensor.filter.averaging.AveragingFilter;
import com.kircherelectronics.fsensor.filter.averaging.MeanFilter;
import com.kircherelectronics.fsensor.sensor.acceleration.LinearAccelerationSensor;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/*
 * AccelerationExplorer
 * Copyright 2018 Kircher Electronics, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class LinearAccelerationSensorLiveData extends LiveData<float[]> {
    private LinearAccelerationSensor sensor;
    private CompositeDisposable compositeDisposable;
    private Context context;
    private AveragingFilter averagingFilter;

    public LinearAccelerationSensorLiveData(Context context) {
        this.context = context;
        this.sensor = new LinearAccelerationSensor(context);
    }

    @Override
    protected void onActive() {
        this.sensor.setSensorFrequency(1);

        averagingFilter = new MeanFilter();
        float filterTimeConstant = (float) 0.5;
        ((MeanFilter) averagingFilter).setTimeConstant(filterTimeConstant);

        this.compositeDisposable = new CompositeDisposable();
        this.sensor.getPublishSubject().subscribe(new Observer<float[]>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(float[] values) {
                if(averagingFilter != null) {
                    setValue(averagingFilter.filter(values));
                } else {
                    setValue(values);
                }
            }

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onComplete() {}
        });
        this.sensor.onStart();
    }

    @Override
    protected void onInactive() {
        this.compositeDisposable.dispose();
        this.sensor.onStop();
    }
}