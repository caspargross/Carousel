/*
 * Copyright (C) 2018 Mauro Di Girolamo
 */

package main.model;

/**
 * A class which represents a sequence in a FASTA file.
 *
 * @author Mauro Di Girolamo
 */
public class FastaSequence {

    /**
     * The identifier of this FASTA sequence which means the first word after the > character in a header.
     */
    private String identifier;

    /**
     * Returns FastaSequence.identifier.
     *
     * @return FastaSequence.identifier
     */
    public String getIdentifier( ) {
        return identifier;
    }


    /**
     * The description of the FASTA sequence which contains the rest of the header line after the identifier with ends
     * trimmed.
     */
    private String description;

    /**
     * Returns FastaSequence.description.
     *
     * @return FastaSequence.description
     */
    public String getDescription( ) {
        return description;
    }


    /**
     * The actual sequence of the FASTA sequence.
     */
    private String sequence;

    /**
     * Returns FastaSequence.sequence.
     *
     * @return FastaSequence.sequence
     */
    public String getSequence( ) {
        return sequence;
    }


    /**
     * The possible codes that a FASTA sequence can be defined over.
     */
    public enum Code {

        /*
        Please read this before adjusting the following definitions:

        1. You should never define a code B positionally before a code A if A subset B holds, because otherwise the automatic detection would never pick code B over code A, since it iterates sequentially over all possible enum values.
           The IUB/IUPAC NucleicAcid code should hence for this reason always be defined before the IUB/IUPAC AminoAcid code.

        2. All alphabetic characters in the code definitions must be upper case, because all alphabetic characters read will be turned into upper case characters.
         */

        /**
         * The definition of the nucleic acid code according to IUB/IUPAC:
         */
        NucleicAcid( "ACGTURYKMSWBDHVNX-" ), //

        /**
         * The definition of the amino acid code according to IUB/IUPAC:
         */
        AminoAcid( "ABCDEFGHIKLMNOPQRSTUVWYZX*-" ); // IUB/IUPAC


        /**
         * Contains the valid characters of a code as defined above.
         */
        private String validCharacters;

        /**
         * Returns Code.validCharacters.
         *
         * @return Code.validCharacters
         */
        public String getValidCharacters( ) {
            return validCharacters;
        }


        /**
         * Creates a new Code object.
         *
         * @param validCharacters the valid characters of the code
         */
        Code( String validCharacters ) {
            this.validCharacters = validCharacters;
            return;
        }

    }

    /**
     * The code of the FASTA sequence.
     */
    private Code code;

    /**
     * Returns FastaSequence.code.
     *
     * @return FastaSequence.code
     */
    public Code getCode( ) {
        return code;
    }


    /**
     * Creates a new FastaSequence ensuring the sequence matches the given or any code throwing an Exception if it does
     * not.
     * <p>
     * If code is passed as null, all possible codes will be tried out sequentially and the first matching code will be
     * set as this FastaSequence object's code.
     *
     * All characters of a sequence will be converted to their respective upper case versions.
     *
     * @param identifier  the identifier of the FASTA sequence
     * @param description the description of the FASTA sequence
     * @param sequence    the actual sequence of the FASTA sequence
     * @param code        the expected code of the FASTA sequence; passing null is a valid choice and results into
     *                    trying to automatically detect the code
     *
     * @throws Exception
     */
    public FastaSequence( String identifier, String description, String sequence, Code code ) throws Exception {
        sequence = sequence.toUpperCase( );
        if( code != null ) { // Match for the given code:
            final String validCharacters = code.getValidCharacters( );
            for( char character : sequence.toCharArray( ) ) { // Loop over all characters to be able to name an invalid character if necessary:
                character = Character.toUpperCase( character );
                if( validCharacters.indexOf( character ) < 0 )
                    throw new Exception( "The sequence with identifier [" + identifier + "] and description [" + description + "] contains the following invalid character for the code [" + code + "]: " + character );
            }
            this.code = code;
            this.sequence = sequence;
        } else { // Try to detect code automatically:
            Code[] codesToTry = Code.values( );
            for( Code codeToTry : codesToTry ) {
                final String validCharacters = codeToTry.getValidCharacters( );
                if( sequence.chars( ).allMatch( ( character ) -> validCharacters.indexOf( Character.toUpperCase( character ) ) >= 0 ) ) {
                    this.code = codeToTry;
                    this.sequence = sequence;
                    break;
                }
            }
            if( this.sequence == null )
                throw new Exception( "The sequence with identifier [" + identifier + "] and description [" + description + "] does not match any of the codes defined in FastaSequence.Code." );
        }
        this.identifier = identifier;
        this.description = description;
        return;
    }

}
