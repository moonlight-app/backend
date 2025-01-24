-- bitwise AND: first & second -> integer
CREATE OR REPLACE FUNCTION BITAND(_first INTEGER, _second INTEGER)
RETURNS INTEGER
LANGUAGE plpgsql AS '
    BEGIN
        RETURN _first & _second;
    END
';
