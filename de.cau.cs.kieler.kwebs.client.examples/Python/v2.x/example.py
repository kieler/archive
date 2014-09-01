from KIELERLayout import KIELERLayout

def main():
    # example graph     
    graph = (
    "{id:\"root\",children:[{id:\"n1\",labels:[{text:\"n1\"}],width:100,height:100}," 
    "{id:\"n2\",labels:[{text:\"n2\"}],width:100,height:50,children:[{id:\"n3\","
    "labels:[{text:\"n3\"}],width:20,height:20},{id:\"n4\",labels:[{text:\"n4\"}],width:20,"
    "height:20}],edges:[{id:\"e4\",source:\"n3\",target:\"n4\"}]}],"
    "edges:[{id:\"e1\",source:\"n1\",target:\"n2\"}]}")

    # some options
    options = {}
    options["spacing"] = 100
    options["algorithm"] = "de.cau.cs.kieler.klay.layered"

    # perform the layout
    kl = KIELERLayout()
    layouted = kl.layout("localhost:9444", "org.json", "org.w3.svg", options, graph)
    
    # just print it to the console
    print(layouted.decode('utf-8'))

if __name__ == '__main__': main()

