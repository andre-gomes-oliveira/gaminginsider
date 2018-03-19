package br.com.andregomesoliveira.gaminginsider.provider;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class FeedsRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new ArticlesProvider(this.getApplicationContext(), intent));
    }
}
