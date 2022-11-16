 exportGmc(){
    this.productService.downloadGmc().subscribe(
      data => {
        download(data);
      },
    )
  }