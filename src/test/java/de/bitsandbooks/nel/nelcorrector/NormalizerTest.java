package de.bitsandbooks.nel.nelcorrector;

import java.io.UnsupportedEncodingException;

import de.bitsandbooks.nel.nelcorrector.util.MyStringUtils;

public class NormalizerTest {

	public static void main(String[] args) throws UnsupportedEncodingException {
		String s = "öäüéèêr̀r̂r̃r̈rʼŕřt̀t̂ẗţỳỹẙyʼy̎ýÿŷp̂p̈s̀s̃s̈s̊sʼs̸śŝŞşšd̂d̃d̈ďdʼḑf̈f̸g̀g̃g̈gʼģq‌​‌​́ĝǧḧĥj̈jʼḱk̂k̈k̸ǩl̂l̃l̈Łłẅẍc̃c̈c̊cʼc̸Çççćĉčv̂v̈vʼv̸b́b̧ǹn̂n̈n̊nʼńņňñm̀m̂m̃‌​m̈‌​m̊m̌ǵß";
		System.out.println(MyStringUtils.normalizeString(s));

	}

}
