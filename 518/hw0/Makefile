all: help

NAME=hw0

help:
	@echo "This makefile is used to repack your $(NAME) code for submission."
	@echo "Type \"make repack netid=YOUR_NETID\" to create a tarball."

repack: clean
ifdef netid
	@echo "Packaging for $(NAME)..."
	@mkdir -p $(NAME)
	@cp -r Makefile analyzecache ult $(NAME)/ 
	@tar zcf $(NAME)-$(netid).tar.gz $(NAME)
	@rm -rf $(NAME)
	@echo "Created $(NAME)-$(netid).tar.gz"
else
	@echo "You must specify a netid, please type \"make help\" for more details."
endif

clean:
	@echo "Cleaning for $(NAME)..."
	@make -C analyzecache clean
	@make -C ult clean
	@rm -rf *.tar.gz $(NAME)
