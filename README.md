BaseClassesBeGone
=================

What is this?
-------------

This is a modification for the popular indie game Minecraft (by Mojang, at minecraft.net). While the game itself, written in Java, is quite fun, it's support for mods is almost non-existant. Most modders work by modifying the original classes and having users manually patch their minecraft.jar archive, which is not the most trivial task for when Grandma just wants Endermen to stop stealing her bridge club re-creation, not to mention issues with how different implementations of Java handle things like META_INF.The biggest problem, however, is that modified classes are distributed 'whole', and will overwrite classes that other modders have changed. This means huge swaths of mods are incompatible with each other.
BaseClassesBeGone (BCBG because acronyms are sexy) handles this by allowing modders to quickly, easily port thier mods to easyily installable patches. BCBG then automatically applies these patches *when the game starts up*, making them dynamic yet easy to remove, unlike a static patcher that would directly modify minecraft.jar.

How does it work?
-----------------

BCBG uses the bytecode manipulatio library ASM to parse patch classes and apply them. Patches are loaded into memory and a cstom classloader is used to provide modified classes seamlessly to the game engine.

Why ASM? BCEL etc.
------------------

ASM is by far the fastest and most memory-effecient bytecode library I could find, outscoring BCBL and JavaAssist on simple and complex patches. When yu're going to be patching hundreds of classes 100 ms a class makes a huge difference in time spent waiting.

Is this endorsed by Notch/Mojang?
---------------------------------

No. This is an entirely unofficial, fan-made mod.

What are your liscensing terms? Can I redistribute this?
--------------------------------------------------------

I haven't picked a liscese yet (help me open source gurus ;_;), so for now just don't please. Link to this repository.

How do I contribute?
--------------------

Just submit a pull request I guess. I'm a shitty project manager.

What are the commit rules?
--------------------------
- Commit unix-style line endings (`git config --global core.autocrlf true`, remove --global if other repos have different rules)
- Use the included .gitignore (is this normal? I think it is... makes sense to me)
- Don't be a dick