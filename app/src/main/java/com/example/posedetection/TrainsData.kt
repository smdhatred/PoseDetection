package com.example.posedetection

import androidx.compose.ui.geometry.Offset

object TrainsData{
    val list = listOf(
        Train(name = "Train One",
            price = "$23.00",
            image = R.drawable.train_one,
            info = "Положение стоя, ноги сдвинуты вплотную, руки располагаются вдоль тела." +
                    " Делается длинный выпад правой ногой. Руки выбрасываются параллельно, затем поднимаются и соединяются над головой. В таком положении делается пауза на несколько секунд. " +
                    "Далее растягиваются мышцы выдвинутой ноги. Осуществляется возврат в начальную позицию. Необходимо проделатьто же самое с другой ногой.",
            pointsConst = mutableListOf(mutableListOf(Offset(300F,150F),Offset(150F,150F)),mutableListOf(Offset(300F,250F),Offset(150F,250F)),mutableListOf(Offset(300F,350F),Offset(150F,350F))),
            imageConst = mutableListOf(R.drawable.pose_1_1, R.drawable.pose_1_2)
        ),
        Train(name = "Train Two",
            price = "$20.00",
            image = R.drawable.train_two,
            info = "Положение стоя, ноги сдвинуты вплотную, руки располагаются вдоль тела." +
                    " Делается длинный выпад правой ногой. Руки выбрасываются параллельно, затем поднимаются и соединяются над головой. В таком положении делается пауза на несколько секунд. " +
                    "Далее растягиваются мышцы выдвинутой ноги. Осуществляется возврат в начальную позицию. Необходимо проделатьто же самое с другой ногой.",
            pointsConst = mutableListOf(mutableListOf(Offset(300F,150F),Offset(150F,150F)),mutableListOf(Offset(300F,150F),Offset(150F,150F))),
            imageConst = mutableListOf(R.drawable.train_one, R.drawable.train_two)
        ),
        Train(name = "Train Three",
            price = "$17.00",
            image = R.drawable.train_three,
            info = "Положение стоя, ноги сдвинуты вплотную, руки располагаются вдоль тела." +
                    " Делается длинный выпад правой ногой. Руки выбрасываются параллельно, затем поднимаются и соединяются над головой. В таком положении делается пауза на несколько секунд. " +
                    "Далее растягиваются мышцы выдвинутой ноги. Осуществляется возврат в начальную позицию. Необходимо проделатьто же самое с другой ногой.",
            pointsConst = mutableListOf(mutableListOf(Offset(300F,150F),Offset(150F,150F)),mutableListOf(Offset(300F,150F),Offset(150F,150F))),
            imageConst = mutableListOf(R.drawable.train_one, R.drawable.train_two)
        )
    )
}