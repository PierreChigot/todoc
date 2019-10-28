package com.cleanup.todoc.ui;

public enum ViewAction {
    NO_TASK,
    // TODO PIERRE Non nécessaire, la vue va toujours afficher les tasks, le ViewAction est là pour
    //  dire qu'il y a une action "non par défaut" qui doit se faire ponctuellement (afficher une pop up, un toast, etc).
    //  Le contrôle de visibilité des éléments à l'écran doit se faire avec un ViewModel, pas un ViewAction !
    SHOW_TASKS


}
