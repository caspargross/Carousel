/**
 * Copyright (C) 2018 Mauro Di Girolamo
 */

package main.model;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.TextCigarCodec;

/**
 * A class which implements several additional Cigar related operations which are not featured in the original htsjdk /
 * samtools library.
 *
 * @author Mauro Di Girolamo
 */
public class CigarOperation {

    /**
     * Given two Cigars, returns a new Cigar concatenating the first Cigar with the second Cigar (hence, the first one
     * will be the leading one).
     *
     * @param anteriorCigar  the first Cigar
     * @param posteriorCigar the second Cigar
     *
     * @return a new Cigar concatenating the two Cigars passed
     */
    public static Cigar concatenateCigars( Cigar anteriorCigar, Cigar posteriorCigar ) {
        return TextCigarCodec.decode( TextCigarCodec.encode( anteriorCigar ) + TextCigarCodec.encode( posteriorCigar ) );
    }

    /**
     * Removes a Cigar's first CigarElement and returns a respective new Cigar, so that the original Cigar stays
     * unmodified.
     *
     * @param cigar the original Cigar
     *
     * @return a respective new Cigar with the first CigarElement removed
     */
    public static Cigar copyAndRemoveFirst( Cigar cigar ) {
        return new Cigar( cigar.getCigarElements( ).subList( 1, cigar.getCigarElements( ).size( ) ) ); // subList: second index exclusive
    }

    /**
     * Removes a Cigar's last CigarElement and returns a respective new Cigar, so that the original Cigar stays
     * unmodified.
     *
     * @param cigar the original Cigar
     *
     * @return a respective new Cigar with the last CigarElement removed
     */
    public static Cigar copyAndRemoveLast( Cigar cigar ) {
        return new Cigar( cigar.getCigarElements( ).subList( 0, cigar.getCigarElements( ).size( ) - 1 ) ); // subList: second index exclusive
    }

}
