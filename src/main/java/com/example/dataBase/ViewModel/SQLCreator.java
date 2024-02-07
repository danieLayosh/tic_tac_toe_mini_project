
package com.example.dataBase.ViewModel;

import com.example.dataBase.Model.BaseEntity;

/**
 * SQLCreator
 */
public interface SQLCreator {
    String CreateSql(BaseEntity entity);
}