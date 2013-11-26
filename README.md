Meemplex
========

A system for asynchronous, distributed development.

Is is modelled around the concept of a Meem.  A Meem has inputs and outputs called Facets.
Meems may depend on each other.

### Meem

A Meem is a unit of functionality.

Comprised of
- configurable properties
- Persisted state
- Lifecycle
- Facets for Meem communication
- dependencies on other Meems and depended on by other meems.

### Facets

Facets are the way Meems communicated with each other.

### Lifecycle

Meems exist in one of several Lifecycle states.

### Dependencies

A dependency defines a relationship between Facets of 2 Meems.  

For a "strong" dependency, the Lifecycle state of the dependent Meem is affected by the state of the
Meem depended on.

### Hyperspace

A hierarchical tree of categories and leaf nodes.  Each node in Hyperspace is a Meem.

### Meemkits

Modules that contain new featres.  In this implementation meemkits are OSGI bundles.

## Quick Start

### Intermajik
Intermajik is a GUI for developing and managing Meems in a MeemSpace.

### Headless version

Run (uses Equinox OSGI framework) ...

    $ telnet localhost 6969
    
    Username: system
    Password: system99
    
    meemspace [smart]> sls();
    Meem not found: hyperSpace:/
    
    meemspace [smart]> createMeemSpace();
    Creating HyperSpace
    Creating Categories in HyperSpace
    Creating Category: /application
    Creating Category: /deployment
    Creating Category: /site
    Creating Category: /user
    Creating Category: /work
    Creating user account: guest
    Creating user account: owner
    Creating Meemkit Hyperspace Categories
    Create MeemSpace elapsed time: 3939 milliseconds
    
    meemspace [smart]> sls();
    deployment/
    work/
    user/
    site/
    application/
    
    meemspace [smart]> installMeemkits();
    
