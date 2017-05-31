.file "mystery.c" 
.text

.globl bar
	.type	bar, @function
bar:
	pushl	%ebp
	movl	%esp, %ebp
	subl	$16, %esp
	cmpl	$1, 8(%ebp)
	jg	.L2
	movl	$0, %eax
	jmp	.L3
.L2:
	movl	$2, -4(%ebp)
	jmp	.L4
.L6:
	movl	8(%ebp), %eax
	movl	%eax, %edx
	sarl	$31, %edx
	idivl	-4(%ebp)
	movl	%edx, %eax
	testl	%eax, %eax
	jne	.L5
	movl	$0, %eax
	jmp	.L3
.L5:
	addl	$1, -4(%ebp)
.L4:
	movl	-4(%ebp), %eax
	cmpl	8(%ebp), %eax
	jl	.L6
	movl	$1, %eax
.L3:
	leave
	ret
	.size	bar, .-bar
.globl foo
	.type	foo, @function
foo:
	pushl	%ebp
	movl	%esp, %ebp
	pushl	%ebx
	subl	$36, %esp
	movl	$0, -12(%ebp)
	movl	$0, -12(%ebp)
	jmp	.L9
.L14:
	movl	-12(%ebp), %eax
	addl	8(%ebp), %eax
	movzbl	(%eax), %eax
	cmpb	$126, %al
	je	.L16
.L10:
	movl	-12(%ebp), %eax
	movl	%eax, %edx
	sarl	$31, %edx
	shrl	$31, %edx
	addl	%edx, %eax
	andl	$1, %eax
	subl	%edx, %eax
	cmpl	$1, %eax
	jne	.L12
	movl	-12(%ebp), %eax
	movl	%eax, %ebx
	addl	8(%ebp), %ebx
	movl	-12(%ebp), %eax
	addl	8(%ebp), %eax
	movzbl	(%eax), %eax
	movsbl	%al, %eax
	movl	%eax, (%esp)
	call	toupper
	movb	%al, (%ebx)
.L12:
	movl	-12(%ebp), %eax
	addl	$1, %eax
	movl	%eax, (%esp)
	call	bar
	testl	%eax, %eax
	je	.L13
	movl	-12(%ebp), %eax
	movl	%eax, %ebx
	addl	8(%ebp), %ebx
	movl	-12(%ebp), %eax
	addl	8(%ebp), %eax
	movzbl	(%eax), %eax
	movsbl	%al, %eax
	movl	%eax, (%esp)
	call	tolower
	movb	%al, (%ebx)
.L13:
	addl	$1, -12(%ebp)
.L9:
	movl	-12(%ebp), %ebx
	movl	8(%ebp), %eax
	movl	%eax, (%esp)
	call	strlen
	cmpl	%eax, %ebx
	jb	.L14
	jmp	.L15
.L16:
	nop
.L15:
	addl	$36, %esp
	popl	%ebx
	popl	%ebp
	ret
	.size	foo, .-foo
	.section	.rodata
	.align 4
.LC0:
	.string	"Incorrect number of command line arguments given"
.LC1:
	.string	"Input:%s"
	.align 4
.LC2:
	.string	"Incorrect format for command line argument"
.LC3:
	.string	"Output: \"%s\"\n"
	.text
.globl main
	.type	main, @function
main:
	pushl	%ebp
	movl	%esp, %ebp
	andl	$-16, %esp
	subl	$32, %esp
	cmpl	$2, 8(%ebp)
	je	.L18
	movl	$.LC0, (%esp)
	call	puts
	movl	$1, %eax
	jmp	.L19
.L18:
	movl	12(%ebp), %eax
	addl	$4, %eax
	movl	(%eax), %eax
	movl	%eax, (%esp)
	call	strlen
	movl	%eax, %edx
	movl	%edx, %eax
	sall	$2, %eax
	addl	%edx, %eax
	movl	%eax, (%esp)
	call	malloc
	movl	%eax, 28(%esp)
	movl	$.LC1, %edx
	movl	12(%ebp), %eax
	addl	$4, %eax
	movl	(%eax), %eax
	movl	28(%esp), %ecx
	movl	%ecx, 8(%esp)
	movl	%edx, 4(%esp)
	movl	%eax, (%esp)
	call	__isoc99_sscanf
	cmpl	$1, %eax
	je	.L20
	movl	$.LC2, (%esp)
	call	puts
	movl	$1, %eax
	jmp	.L19
.L20:
	movl	28(%esp), %eax
	movl	%eax, (%esp)
	call	foo
	movl	$.LC3, %eax
	movl	28(%esp), %edx
	movl	%edx, 4(%esp)
	movl	%eax, (%esp)
	call	printf
	movl	28(%esp), %eax
	movl	%eax, (%esp)
	call	free
	jmp	.L17
.L19:
.L17:
	leave
	ret
	.size	main, .-main
	.ident	"GCC: (GNU) 4.4.7 20120313 (Red Hat 4.4.7-4)"
	.section	.note.GNU-stack,"",@progbits

