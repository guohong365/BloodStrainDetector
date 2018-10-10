package com.uc.activity;

import android.content.Intent;
import android.os.Bundle;

public interface ResultProcessor {
    void process(Intent bundle, RequestParams params);
}
