i) Compilation:
  This project, Prog4.java, does not require any arguments when being ran in the command line. When ran, Prog4
  will open with a 'main menu' containing a list of available options for the user to select from.
  Alternatively, the user may also enter 'exit' to close the program. Once a number option has been selected,
  the user will be prompted for any necessary information required by the chosen option.

  Interpretation:
    The 'equipment' table keeps track of a variable 'INUSE'. A value of 0 represents an Equipment item
    currently NOT in use. A value of 1 represents an Equipment item currently that IS in use. A value of -1
    represents a 'deleted' Equipment item which cannot be rented, but is retained for history. A value of -2
    represents an 'updated' Equipment item which cannot be rented, but is retained for history.

    The 'equiprental' table keeps track of a variable 'RETURNSTATUS'. A value of 0 represents a Rental that has
    been returned and is therefore completed. A value of 1 represents a Rental that is currently being rented,
    has not been returned, and is therefore active. A value of -1 represents a Rental that has been 'deleted'
    and therefore cannot be updated, but is retained for history.

ii) Workload Distribution:
  All members worked together to design the E-R diagram and the general database schema.

  Individually:

  Dylan Carothers II - Responsible for implementing 'Add, update, or delete an equipment inventory record'
    functionality, as well as 'Add, update, or delete an equipment rental record' functionality. This includes
    the methods:
      -editEInvRecord
      -addEInvRecord
      -updateEInvRecord
      -deleteEInvRecord
      -editERentRecord
      -addERentRecord
      -updateERentRecord
      -deleteERentRecord
    All code and comments within these methods are my own (or used from the given JDBC example).

  Gabe Barros - Responsible for implementing add/update/delete a member and add/update/delete skipass. This includes the methods:
    -addMember
    -updateMember
    -deleteMember
    -memberCanBeDeleted
    -addSkipass
    -updateSkipass
    -deleteSkipass
    -skipassCanBeDeleted

  Bronson Housmans - Responsible for creating SQL tables, inserting initial test data, and add/update/delete a lesson registration
  This includes the methods:
    -privateOrGroup
    -lessonReg
