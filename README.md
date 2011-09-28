BaseClassesBeGone
=================

What is this?
-------------

This is a modification for the popular indie game Minecraft (by Mojang, at minecraft.net). While the game itself, written in Java, is quite fun, its support for mods is almost non-existant. Most modders work by modifying the original classes and having users manually patch their minecraft.jar archive, which is not the most trivial task for when Grandma just wants Endermen to stop stealing her bridge club re-creation, not to mention issues with how different implementations of Java handle things like META_INF. The biggest problem, however, is that modified classes are distributed 'whole', and will overwrite classes that other modders have changed. This means huge swaths of mods are incompatible with each other.
BaseClassesBeGone (BCBG because acronyms are sexy) handles this by allowing modders to quickly, easily port their mods to easily installable patches. BCBG then automatically applies these patches *when the game starts up*, making them dynamic yet easy to remove, unlike a static patcher that would directly modify minecraft.jar.

How does it work?
-----------------

BCBG uses the bytecode manipulation library ASM to parse patch classes and apply them. Patches are loaded into memory and a custom classloader is used to provide modified classes seamlessly to the game engine.

Why ASM? BCEL etc.
------------------

ASM is by far the fastest and most memory-effecient bytecode library I could find, outscoring BCBL and JavaAssist on simple and complex patches. When you're going to be patching hundreds of classes 100ms a class makes a huge difference in time spent waiting.

Is this endorsed by Notch/Mojang?
---------------------------------

No. This is an entirely unofficial, fan-made mod.

What are your liscensing terms? Can I redistribute this?
--------------------------------------------------------

I haven't picked a liscese yet (help me open source gurus ;_;), so for now just don't please. Link to this repository.

How do I install/build this?
----------------------------
First you'll need to decompile the launcher and fix all the errors. The obvious ones:
- Decrement all the ints in the Util.getWorkingDirectory switch statement. It should go from 0 - 3, not 1 - 4.
- The doPrivleged blocks are just f'ed up. Most can be commented out, though.
Fix all the otehr errors. The entire launcher doesn't need to work, just enough to login and play.
Now create a directory and clone this repository into it.
Put all the decompiled and fixed files from the launcher into place (DO NOT OVERWRITE GameUpdater.java)
Set up git (refer to github documentation and my rules)  
	git clone git@github.com:Rotten194/BaseClassesBeGone.git  
		makes local copy of bcbg  
	cd BaseClassesBeGone  
	-- add launcher files (won't be tracked by git because they're in .gitignore, WHICH YOU'RE USING RIGHT?!)  
	git branch <yourname>  
	git checkout <yourname>  
	--edit files, commit, etc  
	git checkout master  
	git merge <yourname>  
	--fix any issues  
	--send a [pull request](http://help.github.com/send-pull-requests/)  
	

How do I contribute?
--------------------

Just submit a pull request I guess. I'm a shitty project manager.

What are the commit rules?
--------------------------
- Commit unix-style line endings (`git config --global core.autocrlf true`, remove --global if other repos have different rules)
- Use the included .gitignore (is this normal? I think it is... makes sense to me)
- Don't be a dick