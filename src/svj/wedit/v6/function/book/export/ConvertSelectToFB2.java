package svj.wedit.v6.function.book.export;

import svj.wedit.v6.function.FunctionId;

/**
 * Конвертировать выбранные элементы книги в формат FB2.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 04.02.2020 10:26
 */
public class ConvertSelectToFB2 extends ConvertToFB2 {

    public ConvertSelectToFB2() {
        super (FunctionId.CONVERT_SELECTED_TO_FB2, "Преобразовать выделенное в FB2", "to_fb2.png", true);
    }

}
